#!/usr/bin/python

import os
import re


def check_commit():
    def check_commit_message(message):
        match = re.match('\[#\d+\]', message)
        assert match is not None, "Invalid commit message, please add issue tag [#<number>]. Last commit is '{}'".format(
            message)

    stream = os.popen('git log -n 1 --pretty=%B')
    output = stream.read()
    merge_match = re.match('Merge .+ into .+', output)
    if merge_match:
        print('Merge is matched')
        commit = output.split(' ')[1]
        print('Commit hash is: {}'.format(commit))
        stream = os.popen('git log -1 --pretty=%B {}'.format(commit))
        out = stream.read()
        print('Commit message: {}'.format(out))
        check_commit_message(out)
    else:
        check_commit_message(output)


check_commit()
