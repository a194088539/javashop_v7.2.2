FROM node:8.10.0-slim
MAINTAINER Javashop

RUN mkdir -p /ui/buyer/pc
RUN mkdir -p /ui/ui-domain
RUN mkdir -p /ui/ui-utils

COPY ./buyer/pc /ui/buyer/pc
COPY ./ui-domain /ui/ui-domain
COPY ./ui-utils /ui/ui-utils

ENV NODE_ENV=production

WORKDIR /ui/buyer/pc

ENV HOST 0.0.0.0
EXPOSE 3000

CMD ["npm", "start"]
