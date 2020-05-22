from activityRecognitionComponent import supportVectorMachine


# Class for activity recognition
class activityRecognition(object):
    def __init__(self):
        self.SVM = supportVectorMachine.SVM()

    # Predict dataPoint
    def predictDataPoint(self, dataPoint):
        return self.SVM.predict(dataPoint)

    # Train SVM model
    def trainModel(self, database):
        self.SVM.train(database)
