FROM node

# change or remove proxy to current work setup
ENV http_proxy http://10.12.251.2:8080
ENV https_proxy http://10.12.251.2:8080

# install chrome for tests
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'
RUN apt-get update && apt-get install -yq google-chrome-stable

# clean workspace
# RUN rm -rf /app ||:
# set working directory
RUN mkdir /usr/src/app
WORKDIR /usr/src/app

# add `/usr/src/app/node_modules/.bin` to $PATH
ENV PATH /usr/src/app/node_modules/.bin:$PATH

# install and cache app dependencies
COPY package.json /usr/src/app/package.json
RUN npm install
RUN npm install -g @angular/cli

# clean workspace
RUN rm -fr $(ls -1 --ignore=node_modules .) 
# add app
COPY . /usr/src/app

# run tests
RUN ng test

# start app
CMD ng serve --host 0.0.0.0

# at the constants.ts file change the endpoint to the host's ip address (127.0.0.1)
# docker build -t angular:1.0 .
# docker run -d -p 4200:4200 angular:1.0