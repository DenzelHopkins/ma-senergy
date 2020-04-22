from src.activityDiscoveryComponent import outlierDetection, onlineClustering


class activityDiscovery(object):
    def __init__(self):
        self.onlineCluster = onlineClustering.OnlineCluster(11)
        self.outlierDetection = outlierDetection.OneSVM()

    def detectOutlier(self, dataPoint):
        return self.outlierDetection.predict(dataPoint)

    def clusterDataPoint(self, dataPoint, time):
        return self.onlineCluster.cluster(dataPoint, time)

    def discover(self, dataPoint, database, label, time):
        if self.outlierDetection.model is not None:
            knownDataPoint = self.outlierDetection.predict(dataPoint)
            if knownDataPoint[0] == -1:
                foundedActivity = self.clusterDataPoint(dataPoint, time)
                if foundedActivity is not None:
                    database.write(foundedActivity.to_json(orient='records'), time, label)
                    self.outlierDetection.train(database)
                    return label
        else:
            foundedActivity = self.clusterDataPoint(dataPoint, time)
            if foundedActivity is not None:
                database.write(foundedActivity.to_json(orient='records'), time, label)
                self.outlierDetection.train(database)
                return label

    def modifiedDiscover(self, dataPoint, database, label, time):
        if self.outlierDetection.model is not None:
            knownDataPoint = self.outlierDetection.predict(dataPoint)
            if knownDataPoint[0] == -1:
                foundedActivity = self.clusterDataPoint(dataPoint, time)
                if foundedActivity is not None:
                    database.write(foundedActivity.to_json(orient='records'), time, label)
                    self.outlierDetection.train(database)
                    return label, False
                else:
                    return None, False
            else:
                return None, True
        else:
            foundedActivity = self.clusterDataPoint(dataPoint, time)
            if foundedActivity is not None:
                database.write(foundedActivity.to_json(orient='records'), time, label)
                self.outlierDetection.train(database)
                return label, False
            else:
                return None, False

