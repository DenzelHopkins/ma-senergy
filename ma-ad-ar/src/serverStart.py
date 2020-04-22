import json

from flask import Flask, jsonify, request
from flask_cors import CORS
import pandas as pd

from src.activityRecognitionComponent import activityRecognitionAPI
from src.activityDiscoveryComponent import activityDiscoveryAPI
from src import databaseAPI, annotation

import activityDiscovery
import activityRecognition

app = Flask(__name__)
cluster = activityDiscovery.OnlineCluster(11)
svm = activityRecognition.SVM()
CORS(app)

# Initialize components
training = True
activityRecognition = activityRecognitionAPI.activityRecognition()
activityDiscovery = activityDiscoveryAPI.activityDiscovery()
database = databaseAPI.database()


@app.route("/analyseDataPoint", methods=["POST"])
def discovery():
    if request.method == "POST":

        # Read data from the request
        data = request.get_json(force=True)
        dataPoint = pd.Series(data['feature'])
        time = dataPoint.iloc[-1]
        dataPoint = dataPoint[:-1]

        # Object for the answer
        answer = {'recognizedActivity': 'No activity', 'discoveredActivity': 'No activity'}

        # Check if trainingDuration is over
        global training
        if training != data['training']:
            activityRecognition.trainModel(database)
            training = False

        # Annotate dataPoint
        label = annotation.labelDataPoint(dataPoint)

        # When use the integrated system
        resultActivityDiscovery = activityDiscovery.discover(dataPoint, database, label, time)
        if resultActivityDiscovery:
            answer['discoveredActivity'] = resultActivityDiscovery
            print(resultActivityDiscovery)
            if not training:
                activityRecognition.trainModel(database)
        if not training:
            resultActivityRecognition = activityRecognition.predictDataPoint(dataPoint)
            answer['recognizedActivity'] = resultActivityRecognition

        # Return answer
        return jsonify(answer)


if __name__ == "__main__":
    app.run(port=5000)
