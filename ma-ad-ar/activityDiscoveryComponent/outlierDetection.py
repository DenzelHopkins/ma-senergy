import numpy as np
from sklearn.svm import OneClassSVM


# Class for one-class-support-vector-machine
class OneSVM(object):
    def __init__(self):
        self.X = []
        self.segment = []
        self.model = None
        self.known = 0
        self.unknown = 0

    # Predict dataPoint
    def predict(self, segment):
        point = []
        for n in segment:
            point.append(float(n))
        point = [np.array(point)]
        return self.model.predict(point)

    # Train model
    def train(self, database):
        data = database.get()
        if data.size > 1:
            self.X = []
            for point in data:
                self.segment = []
                for n in point['segment'].strip("[]").split(','):
                    self.segment.append(float(n))
                self.segment = np.array(self.segment)
                self.X.append(self.segment)
            self.X = np.vstack(self.X)
            self.model = OneClassSVM(nu=0.07).fit(self.X)
