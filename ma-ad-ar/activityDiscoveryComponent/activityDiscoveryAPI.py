from activityDiscoveryComponent import outlierDetection, onlineClustering


# Class for activity discovery
class activityDiscovery(object):
    def __init__(self):
        self.onlineCluster = onlineClustering.OnlineCluster(11)
        self.outlierDetection = outlierDetection.OneSVM()

    # Detect outlier in dataPoint
    def detectOutlier(self, dataPoint):
        return self.outlierDetection.predict(dataPoint)

    # Cluster dataPoint
    def clusterDataPoint(self, dataPoint, time):
        return self.onlineCluster.cluster(dataPoint, time)

    # Discover dataPoint when using integratedSystem
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

    # Discover dataPoint when using modifiedSystem
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
