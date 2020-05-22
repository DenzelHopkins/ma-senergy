import heapq
import math
import operator
import scipy

# Delete cluster after one week inactivity
memoryDelta = 604800000


# Measure distance between two dataPoints
def kernel_gauss(a, b, sigma=0.1):
    v = a - b
    return math.exp(-sigma * (math.sqrt(scipy.square(v).sum()) ** 2))


# Class for a cluster
class Cluster(object):
    def __init__(self, segment, time):
        self.center = segment
        self.size = kernel_gauss(segment, segment)
        self.timestampEnd = time
        self.timestampStart = time
        self.firstPoint = segment
        self.endPoint = segment
        self.num_points = 1

    def add(self, segment, time):
        self.size += kernel_gauss(self.center, segment)
        self.center += (segment - self.center) / self.size
        self.timestampEnd = time
        self.endPoint = segment
        self.num_points += 1

    def merge(self, c):
        self.center = (self.center * self.size + c.center * c.size) / (self.size + c.size)
        self.size += c.size
        self.num_points += c.num_points
        self.num_points -= 1


# Class for the distances
class Distance(object):
    def __init__(self, x, y, d):
        self.x = x
        self.y = y
        self.d = d

    def __lt__(self, o):
        return self.d < o.d

    def __str__(self):
        return "Dist(%f)" % self.d


# Class for the OnlineClustering
class OnlineCluster(object):
    def __init__(self, N):
        self.currentClusters = []
        self.allClusters = []
        self.n = 0
        self.N = N
        # cache inter-cluster distances
        self.distanceClusters = []

    def cluster(self, segment, time):

        # Delete old cluster (depends on memoryDelta)
        for clusterI in self.currentClusters:
            if (time + 1) - clusterI.timestampEnd >= memoryDelta:
                self.currentClusters.remove(clusterI)
                self.removeDistance(clusterI)
                self.allClusters.append(clusterI)

        # Find the closest cluster
        if len(self.currentClusters) > 0:
            closestArray = [(i, kernel_gauss(c.center, segment)) for i, c in enumerate(self.currentClusters)]
            closest = self.currentClusters[max(closestArray, key=operator.itemgetter(1))[0]]

            if max(closestArray, key=operator.itemgetter(1))[1] > 0.75:
                closest.add(segment, time)
                if closest.num_points > 3:
                    self.currentClusters.remove(closest)
                    self.removeDistance(closest)
                    self.allClusters.append(closest)
                    return closest.center

        # Delete one cluster when there are to many
        if len(self.currentClusters) > self.N:
            m = heapq.heappop(self.distanceClusters)
            self.currentClusters.remove(m.y)
            self.removeDistance(m.y)
            m.x.merge(m.y)
            self.updateDistance(m.x)

        # Make a new cluster for the current segment
        newCluster = Cluster(segment, time)
        self.currentClusters.append(newCluster)
        self.updateDistance(newCluster)
        self.n += 1

    # Delete a current cluster
    def removeDistance(self, c):
        r = []
        for h in self.distanceClusters:
            if h.x == c or h.y == c:
                r.append(h)
        for h in r:
            self.distanceClusters.remove(h)
            heapq.heapify(self.distanceClusters)

    # Update distances between all clusters
    def updateDistance(self, c):
        self.removeDistance(c)
        for x in self.currentClusters:
            if x == c:
                continue
            d = kernel_gauss(x.center, c.center)
            t = Distance(x, c, d)
            heapq.heappush(self.distanceClusters, t)
