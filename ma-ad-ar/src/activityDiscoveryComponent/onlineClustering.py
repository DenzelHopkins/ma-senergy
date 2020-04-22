import heapq
import math
import numpy as np
import operator

import scipy

memoryDelta = 604800000  # cluster sollen nach einer Woche Inaktivität gelöscht werden (in Millisekunden)


def kernel_gauss(a, b, sigma=0.1):
    v = a - b
    return math.exp(-sigma * (math.sqrt(scipy.square(v).sum()) ** 2))


class OnlineVariance(object):
    """
    Welford's algorithm computes the sample variance incrementally.
    """

    def __init__(self, iterable=None, ddof=1):
        self.ddof, self.n, self.mean, self.M2, self.std = ddof, 0, 0.0, 0.0, 0.0

    def std_calc(self, datum):
        self.n += 1
        self.delta = datum - self.mean
        self.mean += self.delta / self.n
        self.M2 += self.delta * (datum - self.mean)
        self.variance = self.M2 / (self.n - self.ddof)
        self.std = np.sqrt(self.variance)

    def merge_std(self, new):
        n_mean = (self.n * self.mean + new.n * new.mean) / (self.n + new.n)
        self.variance = (self.n * self.std * self.std + new.n * new.std * new.std + self.n * (self.mean - n_mean) * (
                self.mean - n_mean) + new.n * (new.mean - n_mean) * (new.mean - n_mean)) / (self.n + new.n)
        self.std = np.sqrt(self.variance)
        self.M2 = self.variance * (self.n + new.n - self.ddof)
        self.n += new.n
        self.mean = n_mean


class Cluster(object):
    def __init__(self, segment, time):
        self.center = segment
        self.size = kernel_gauss(segment, segment)
        self.timestampEnd = time
        self.timestampStart = time
        self.firstPoint = segment
        self.endPoint = segment
        self.num_points = 1
        self.STD = OnlineVariance(ddof=0)
        self.STD.std_calc(kernel_gauss(segment, segment))

    def add(self, segment, time):
        self.size += kernel_gauss(self.center, segment)
        self.center += (segment - self.center) / self.size
        self.timestampEnd = time
        self.endPoint = segment
        self.num_points += 1
        self.STD.std_calc(kernel_gauss(self.center, segment))

    def merge(self, c):
        self.center = (self.center * self.size + c.center * c.size) / (self.size + c.size)
        self.size += c.size
        self.num_points += c.num_points
        self.num_points -= 1
        self.STD.merge_std(c.STD)


class Distance(object):
    """this is just a tuple,
    but we need an object so we can define cmp for heapq"""

    def __init__(self, x, y, d):
        self.x = x
        self.y = y
        self.d = d

    def __lt__(self, o):
        return self.d < o.d

    def __str__(self):
        return "Dist(%f)" % self.d


class OnlineCluster(object):
    def __init__(self, N):
        self.currentClusters = []
        self.allClusters = []
        self.n = 0
        self.N = N
        # cache inter-cluster distances
        self.distanceClusters = []

    def cluster(self, segment, time):

        # delete old cluster (depends on memoryDelta)
        for clusterI in self.currentClusters:
            if (time + 1) - clusterI.timestampEnd >= memoryDelta:
                self.currentClusters.remove(clusterI)
                self.removeDistance(clusterI)
                self.allClusters.append(clusterI)

        # find the closest cluster
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

        # delete one cluster when there are to many
        if len(self.currentClusters) > self.N:
            m = heapq.heappop(self.distanceClusters)
            self.currentClusters.remove(m.y)
            self.removeDistance(m.y)
            m.x.merge(m.y)
            self.updateDistance(m.x)

        # make a new cluster for the current segment
        newCluster = Cluster(segment, time)
        self.currentClusters.append(newCluster)
        self.updateDistance(newCluster)
        self.n += 1

    def removeDistance(self, c):
        """invalidate intercluster distance cache for c"""
        r = []
        for h in self.distanceClusters:
            if h.x == c or h.y == c:
                r.append(h)
        for h in r:
            self.distanceClusters.remove(h)
            heapq.heapify(self.distanceClusters)

    def updateDistance(self, c):
        """Cluster c has changed, re-compute all intercluster distances"""
        self.removeDistance(c)

        for x in self.currentClusters:
            if x == c:
                continue
            d = kernel_gauss(x.center, c.center)
            t = Distance(x, c, d)
            heapq.heappush(self.distanceClusters, t)
