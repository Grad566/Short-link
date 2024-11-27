FROM gradle:8.7.0-jdk17

WORKDIR /

COPY / .

RUN gradle installBootDist

CMD ./build/install/shorter-boot/bin/shorter