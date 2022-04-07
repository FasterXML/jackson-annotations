We appreciate issues as very valuable contributions, but just to make sure here are things that are important to do before filing an issue:

* Only report issues (and perhaps request new features, FEATURE)
* Usage questions should be asked on [Jackson-user](https://groups.google.com/g/jackson-user) list -- you are more likely to get help that way (and we will promptly close questions-as-issues)
* This repo -- `jackson-annotations` -- only defines annotation types, and actual implementation of behavior is within `jackson-databind`, so very often this is NOT THE PLACE TO FILE AN ISSUE.
* Check to see if this issue has already been reported (quick glance at existing issues): no deep search necessary, just quick sanity check
* Include version information for Jackson version you use
* (optional but highly recommended) Verify that the problem occurs with the latest patch of same minor version; and even better, if possible, try using the latest stable patch version
    * For example: if you observe an issue with version `2.4.1`, first upgrade to `2.4.6` to ensure problem has not already been fixed.
