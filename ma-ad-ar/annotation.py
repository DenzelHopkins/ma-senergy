import string
import numpy as np


def labelDataPoint(dataPoint):
    label = ''
    result = np.where(dataPoint == 1)
    alphabet = list(string.ascii_uppercase)
    for x in result[0]:
        label = label + alphabet[x]
    return label


