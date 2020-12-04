#!/usr/bin/python

import os
import sys

import docker
import psutil

from config import Configuration, IMAGE, TAG

CONFIGURATION = Configuration()
DOCKER_CLIENT = docker.from_env()


def __check_running_process(process_name):
    for proc in psutil.process_iter():
        try:
            if process_name.lower() in proc.name().lower():
                return True
        except (psutil.NoSuchProcess, psutil.AccessDenied, psutil.ZombieProcess):
            pass
    return False


def log(severity, tag, message):
    # type:(str, str, str) -> None
    print("[{}] [{}]: {}".format(severity, tag, message))


def inform(severity, before_message=None, after_message=None, tag=None):
    def decorator(func):
        def wrapper(*args, **kwargs):
            log_tag = tag
            if not log_tag:
                log_tag = func.__name__.upper()
            if before_message:
                log(severity, log_tag, before_message)
            response = func(*args)
            if response is not None:
                log(severity, log_tag, "Returned value: {}".format(response))
            if after_message:
                log(severity, log_tag, after_message)
            return response

        return wrapper

    return decorator


@inform('INFO', 'Try to download images', 'Docker images are downloaded', tag='DOCKER')
def download_dockers():
    for docker_image in CONFIGURATION.docker_images:
        DOCKER_CLIENT.images.pull(docker_image[IMAGE], tag=docker_image.get(TAG))
        log('INFO', 'DOCKER', '{} is download'.format(docker_image[IMAGE]))


@inform('INFO', 'Try to start docker containers', tag='DOCKER')
def start_dockers():
    # type: ()-> bool
    result = os.system('docker-compose up -d web api')
    return result == 0


@inform('INFO', 'Stop old containers', 'Old containers are stopped', tag='DOCKER')
def stop_old_dockers(containers_info):
    if containers_info:
        os.system('cd {} && docker-compose stop'.format(containers_info))


def delete_old_dockers(containers_info):
    os.system('docker-compose rm -f')
    os.system('docker system prune -af')


@inform('INFO', 'Start daemon', 'Daemon is started')
def start_daemon():
    if not __check_running_process('update_daemon.py'):
        os.system('./update_daemon.py &')


def start_old_dockers(prev_version_info):
    pass


@inform('INFO', 'Get docker', tag='MAIN')
def get_old_docker_info():
    return DOCKER_CLIENT.containers.list()


@inform('INFO', 'Start update', 'Update is finished', tag='MAIN')
def main(info):
    log('INFO', 'MAIN', 'param: {}'.format(info))
    download_dockers()
    old_containers_info = get_old_docker_info()
    stop_old_dockers(info)
    status = start_dockers()
    if status:
        delete_old_dockers(info)
        start_daemon()
    else:
        log('ERROR', 'MAIN', 'unable to start new dockers')
        start_old_dockers(None)


if __name__ == '__main__':
    if len(sys.argv) > 1:
        info = sys.argv[1]
    else:
        info = None
    main(info)
