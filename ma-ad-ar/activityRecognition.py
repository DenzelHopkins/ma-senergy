from sklearn import svm
from sklearn.model_selection import train_test_split

import numpy as np


class SVM(object):
    def __init__(self):
        self.labels = ["Meal_Preparation",
                       "Relax",
                       "Eating",
                       "Work",
                       "Sleeping",
                       "Wash_Dishes",
                       "Bed_to_Toilet",
                       "Enter_Home",
                       "Leave_Home",
                       "Housekeeping",
                       "Resperate"]
        self.X = []
        self.y = []
        self.segment = []

        self.X_train = []
        self.X_test = []
        self.y_train = []
        self.y_test = []

        self.model = None

    def predict(self, segment):

        point = []
        for n in segment:
            point.append(float(n))
        point = [np.array(point)]

        label = self.model.predict(point)
        return label

    def train(self):
        # data = dbAPI.get(25) get Data with Labels!
        self.X = []
        self.y = []

        for point in data:
            self.segment = []

            for n in point['segment'].strip("[]").split(','):
                self.segment.append(float(n))
            self.segment = np.array(self.segment)

            self.X.append(self.segment)
            self.y.append(point['label'])

        self.X = np.vstack(self.X)

        self.X_train, self.X_test, self.y_train, self.y_test = \
            train_test_split(self.X, self.y, random_state=0)

        self.model = svm.SVC(kernel='poly', probability=True).fit(self.X_train, self.y_train)
