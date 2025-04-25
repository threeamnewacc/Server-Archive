import re


def valid_youtube_link(link):
    pattern = re.compile("(?:https|http)\:\/\/(?:[\w]+\.)?youtube\.com\/(?:c\/|channel\/|user\/)?([a-zA-Z0-9\-]{1,})")
    return pattern.match(link)


def valid_twitter_link(link):
    pattern = re.compile("(?:https|http)\:\/\/(?:[\w]+\.)?twitter\.com\/(?:(?:\w)*#!\/)?(?:pages\/)?(?:[\w\-]*\/)*([\w\-]*)")
    return pattern.match(link)
