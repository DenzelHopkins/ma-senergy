FROM maven:3.6.1-jdk-8

COPY ma-ad-ar         /usr/src/app/python/
COPY ma-preprocessing /usr/src/app/

WORKDIR /usr/src/app/python

# install python
RUN apt-get -y update && apt-get -y install python3 python3-pip cython supervisor python3-tk gcc

RUN pip3 install --upgrade pip setuptools wheel
RUN pip3 install -r requirements.txt

WORKDIR /usr/src/app

# build java package
RUN mvn -C clean package

RUN mkdir -p /var/log/supervisor

COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

CMD /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf
