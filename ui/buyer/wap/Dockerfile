FROM node:8.10.0-slim
MAINTAINER Javashop

RUN mkdir -p /ui/buyer/wap
RUN mkdir -p /ui/ui-domain
RUN mkdir -p /ui/ui-utils

COPY ./buyer/wap /ui/buyer/wap
COPY ./ui-domain /ui/ui-domain
COPY ./ui-utils /ui/ui-utils

ENV NODE_ENV=production

WORKDIR /ui/buyer/wap

ENV HOST 0.0.0.0
EXPOSE 3000

CMD ["npm", "start"]
