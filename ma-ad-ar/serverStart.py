import json

from flask import Flask, jsonify, request
from flask_cors import CORS
import pandas as pd

import activityDiscovery
import activityRecognition

app = Flask(__name__)
cluster = activityDiscovery.OnlineCluster(11)
svm = activityRecognition.SVM()
CORS(app)

set_start_time = False
start_time = 0

# 1 week
trainings_duration = 604800000


# 1 month
# trainings_duration = 2628000000

@app.route("/test", methods=["GET"])
def test():
    if request.method == "GET":
        return jsonify({'answer': 'Server läuft!'})


@app.route("/discovery", methods=["POST"])
def discovery():
    if request.method == "POST":
        data = request.get_json(force=True)
        data = pd.Series(data['feature'])
        time = data.iloc[-1]
        data = data[:-1]

        # check if trainingstime passed or not
        global set_start_time
        global start_time
        if set_start_time is not True:
            start_time = time
            set_start_time = True
        diff = time - start_time

        # answer
        answer = {}

        # activity discovery
        jsonAD = {}
        answer_ad = cluster.cluster(data, time)
        if answer_ad:
            if diff > trainings_duration:
                svm.train()
            jsonAD['data'] = json.dumps(data.tolist())
            jsonAD['time'] = time
        else:
            jsonAD['data'] = None
            jsonAD['time'] = None

        # # activity recognition
        # jsonAR = {}
        # if svm.model is not None:
        #     pred_label = svm.predict(data)
        #     jsonAR['label'] = pred_label
        # else:
        #     jsonAR['label'] = None

        answer['activityDiscovery'] = jsonAD
        # answer['activityRecognition'] = jsonAR

        return answer


if __name__ == "__main__":
    app.run(port=5000)
