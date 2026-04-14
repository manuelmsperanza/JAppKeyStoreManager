# Feature Notes

## Java 25 baseline

The project is now configured to build with Java 25 by using Maven's `release` flag.

## Build reliability improvements

- Removed strict enforcer execution that blocked `mvn install` in restricted environments.
- Refreshed Maven plugin versions used for compile, assembly, release, and jarsigner tasks.

## Test coverage improvement

A new `JksFilterTest` verifies expected behavior for:

- Accepting directories,
- Accepting `.jks` files,
- Rejecting non-keystore extensions.

## Documentation quality

Javadoc was expanded in source classes and tests to avoid missing/misleading API docs.
