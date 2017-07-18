# Set the base image to Ubuntu
FROM ubuntu:14.04

#FOR DEBUGGING
RUN apt-get update -y
RUN apt-get upgrade -y

RUN apt-get install nodejs -y
RUN apt-get install nodejs-legacy -y
RUN apt-get install npm -y

# Install nodemon
RUN npm install -g nodemon

# Provides cached layer for node_modules
RUN mkdir -p /usr/src/app

# Installing hebrew
# RUN sudo locale-gen heb_IL
# RUN sudo locale-gen heb_IL.utf8

# Define working directory
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN npm install

# Expose port
EXPOSE  3001

# Run app using nodemon
CMD ["nodemon", "/usr/src/app/server.js"]




########################################
# Define default command.
#CMD ["bash"]
#FROM node:boron

# Create app directory
#RUN mkdir -p /usr/src/app
#WORKDIR /usr/src/app

# Install app dependencies
#COPY package.json /usr/src/app/
#RUN npm install

#installing hebrew
# RUN sudo apt-get update; sudo apt-get install user-he

# Bundle app source
#COPY . /usr/src/app

#EXPOSE 8080
#CMD [ "npm", "start" ]

#FROM    centos:centos6

# Enable EPEL for Node.js
#RUN     rpm -Uvh http://download.fedoraproject.org/pub/epel/6/i386/epel-release-6-8.noarch.rpm
# Install Node.js and npm
#RUN     yum install -y npm

# Bundle app source
#COPY . /src

#EXPOSE  3001

#CMD ["node", "/src/server.js"]
