import numpy as np
from sklearn import svm

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
                       "Resperate",
                       "Other"]
        self.X = []
        self.y = []
        self.segment = []

        self.X_res = []
        self.y_res = []

        self.model = None

    def predict(self, segment):
        point = []
        for n in segment:
            point.append(float(n))
        point = [np.array(point)]
        label = self.model.predict(point)
        return label[0]

    def train(self, database):
        data = database.get()
        if data.size > 1:
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

            self.model = svm.SVC(kernel='rbf', probability=True).fit(self.X, self.y)
