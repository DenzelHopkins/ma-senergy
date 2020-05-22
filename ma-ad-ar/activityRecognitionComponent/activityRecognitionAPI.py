from activityRecognitionComponent import supportVectorMachine


class activityRecognition(object):
    def __init__(self):
        self.SVM = supportVectorMachine.SVM()

    def predictDataPoint(self, dataPoint):
        return self.SVM.predict(dataPoint)

    def trainModel(self, database):
        self.SVM.train(database)
