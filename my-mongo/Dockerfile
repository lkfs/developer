FROM openjdk:8

ENV user app
ENV app my-mongo
ENV port 8383
ENV namespace test

RUN mkdir -p /home/${user}/logs \
    mkdir -p /home/${user}/nacos/naming/${namespace}/ \
    && groupadd ${user} \
    && useradd -d /home/${user} -g ${user} ${user} \
    && echo "nohup java -jar -Duser.home=/home/${user} ${app}.jar" > /home/${user}/start.sh

COPY ./target/${app}-application-1.0.jar /home/${user}/${app}.jar

RUN chown -R ${user} /home/${user} && chmod -R a+rwx /home/${user}

USER ${user}

EXPOSE ${port}
VOLUME ["/home/${user}/logs","/etc"]

WORKDIR /home/${user}
ENTRYPOINT ["sh", "./start.sh", "&"]
