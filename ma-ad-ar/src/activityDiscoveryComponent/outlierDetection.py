import numpy as np
from sklearn.svm import OneClassSVM


class OneSVM(object):
    def __init__(self):
        self.X = []
        self.segment = []
        self.model = None
        self.known = 0
        self.unknown = 0

    def predict(self, segment):
        point = []
        for n in segment:
            point.append(float(n))
        point = [np.array(point)]

        sol = self.model.predict(point)

        if sol[0] == -1:
            self.unknown += 1
        else:
            self.known += 1

        return self.model.predict(point)

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

    def activities(self):
        return self.unknown, self.known
