#!/usr/bin/python


import os
import sys
import time
from zipfile import ZipFile

import requests
import wget as wget

from config import Configuration

CONFIGURATION = Configuration()


def __extract_all_with_permission(zf, target_dir):
    ZIP_UNIX_SYSTEM = 3
    for info in zf.infolist():
        extracted_path = zf.extract(info, target_dir)

        if info.create_system == ZIP_UNIX_SYSTEM:
            unix_attributes = info.external_attr >> 16
            if unix_attributes:
                os.chmod(extracted_path, unix_attributes)


def __get_new_release_data(uri):
    response = requests.get(uri)
    if response.status_code // 100 != 2:
        return None
    return response.json()


def check_update(release_uri, current_version):
    # type: (str, str)->bool
    def is_higher(check_version):
        # type: (str)->bool
        check = check_version.split('.')
        current = current_version.split('.')
        for i in range(min(len(current), len(check))):
            if check[i] > current[i]:
                return True
        return len(check) > len(current)

    release_data = __get_new_release_data(release_uri)
    if not release_data:
        return False
    tag_name = release_data["tag_name"]
    return is_higher(tag_name.replace("v", ""))


def download_update(uri):
    # type: (str)-> str
    release_data = __get_new_release_data(uri)
    download_uri = release_data['assets'][0]['browser_download_url']
    version = release_data['tag_name']
    name = download_uri.split('/')[-1]
    zip_file_path = "../{}".format(name)
    wget.download(download_uri, zip_file_path)
    path = '../{}'.format(version)
    with ZipFile(zip_file_path) as zip_file:
        __extract_all_with_permission(zip_file, path)
    os.system('rm {}'.format(zip_file_path))
    return path


def run_pre_update_checks(path):
    # type: (str)->bool
    result = os.system("cd {} && ./pre_update.py".format(path))
    return result == 0


def start_update(path='.'):
    os.system('cd {} && pip install -r requirements.txt')
    current_path = sys.path[0]
    os.system('cd {} && ./install.py {} &'.format(path, current_path))


def main():
    while not check_update(CONFIGURATION.release_uri, CONFIGURATION.version):
        time.sleep(CONFIGURATION.check_time)
    # TODO: write that update is found
    time.sleep(CONFIGURATION.check_time)
    path_to_new_version = download_update(CONFIGURATION.release_uri)
    status = run_pre_update_checks(path_to_new_version)
    if status:
        start_update(path_to_new_version)


if __name__ == '__main__':
    main()
