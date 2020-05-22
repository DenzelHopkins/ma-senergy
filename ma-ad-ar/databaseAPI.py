import numpy as np


# Class to hold and read annotated dataPoints
class database(object):

    def __init__(self):
        self.savedData = []

    def write(self, data, time, label):
        point = {"segment": data,
                 "time": time,
                 "label": label}
        self.savedData.append(point)

    def clear(self):
        self.savedData.clear()

    def get(self):
        return np.array(self.savedData)
