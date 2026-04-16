# Feature Notes

## Java 25 baseline

The project is now configured to build with Java 25 by using Maven's `release` flag.

## Build reliability improvements

- Kept enforcer checks, but updated them to modern version ranges (`Maven >= 3.9`, `Java >= 25`).
- Moved jarsigner executions into an opt-in `sign-artifacts` profile so local `mvn install` no longer requires signing secrets.

## Test coverage improvement

A new `JksFilterTest` verifies expected behavior for:

- Accepting directories,
- Accepting `.jks` files,
- Rejecting non-keystore extensions.

## Documentation quality

Javadoc was expanded in source classes and tests to avoid missing/misleading API docs.
