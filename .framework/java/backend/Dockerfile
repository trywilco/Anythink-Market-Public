FROM openjdk:11

RUN curl https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh --output /wait-for-it.sh

RUN chmod +x /wait-for-it.sh

#COPY /backend /usr/src/backend

WORKDIR /usr/src
#COPY backend ./backend
#COPY .wilco ./.wilco

# Pre-install npm packages
WORKDIR /usr/src/backend

