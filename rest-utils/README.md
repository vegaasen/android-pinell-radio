Restinator - REST-based utils
==========

Simple utilities for performing just about every REST-related operation out there (jeje, some of them are missing, I know :-P)

# Introduction

This stuff is still under the making. More details will follow as soon as I get more time to finish this thing off.

## Background

I really needed a REST-like util at work, so I started working on this little framework. It is quite easy and uses nothing
but POJOs, so it should therefore be quite fast and contain a small fingerprint.

The idea behind this thing is to make REST-calls as easy as possible. I have not included any JSON-libraries, so that is something
that each of the users/projects needs to take care of themselves.

I used GSON together with this thing, and it works just as I want it to work! Otherwise, I started off using the

    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20140107</version>
    </dependency>

..library, however I find this a bit slow, annoying and crappy ;-P So go for GSON!

    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.2.4</version>
    </dependency>

# Usage

## Simplest possible

    new Restinator("http://www.rest.com/my/rest/service").get();

## Simple GET with a URL-parameter

    new Restinator("http://www.rest.com/my/rest/service/{my-param}")
        .urlParam(new UrlParam("my-param", "123456789"))
        .get();

## Simple GET with a header

    new Restinator("http://www.rest.com/my/rest/service/{my-param}")
        .urlParam(new UrlParam("my-param", "123456789"))
        .header(new Header("User-Agent", "my h4x0r c0mpu70r"))
        .get();

## Simple POST with with a URL-parameter

    new Restinator("http://www.rest.com/my/rest/service/{my-param}")
        .urlParam(new UrlParam("my-param", "123456789"))
        .param(new Param("some-post-param", "that makes sense", "and one more of this one please.."))
        .post();

# Requirements

None! All is POJOs etc., so no special requirements is needed within your POM to make use of this little library.
Neat, huh?? Jeah :-)

# Notes

Example of (hopefully) usage:

    new Restinator(<uri>)
        .params(
            Param
                .add("","")
                .add("",""))
        .headers(
            Header
                .add("","")
                .add("",""))
        .authentication(
            Authentication.add(BASIC, User.create("", ""))
        .get();

# Suggestions

Send them to:

- Vegard Aasen - vegaasen@gmail.com

.. Or just write an issue, of course!