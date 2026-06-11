# AGENTS.md

## Repository structure

This repository contains three branches with different dependency stacks:

| Branch | Stack | Description |
|--------|-------|-------------|
| `main` | Framework-agnostic | Core HTML DSL only, zero dependencies |
| `spring-boot-2` | Spring Boot 2 / Spring 5 / javax | Core + Spring MVC integration |
| `spring-boot-3` | Spring Boot 3 / Spring 6 / jakarta | Core + Spring MVC integration |

`spring-boot-2` is based on `main` with Spring MVC code merged in.
`spring-boot-3` is based on `spring-boot-2` with Spring 6 / jakarta migration applied.

## Recursive code propagation

Branches form a chain: `main` → `spring-boot-2` → `spring-boot-3`.

When changes are made to a branch, they must be merged forward into downstream branches sequentially:

1. **Changes on `main`** → merge `main` into `spring-boot-2`, then merge `spring-boot-2` into `spring-boot-3`
2. **Changes on `spring-boot-2`** → merge `spring-boot-2` into `spring-boot-3`
3. **Changes on `spring-boot-3`** → no propagation needed

Example: after committing to `main`:
```
git checkout spring-boot-2 && git merge main
git checkout spring-boot-3 && git merge spring-boot-2
```

After each merge, run `mvn clean test` to verify.

### Handling merge conflicts

During propagation, `pom.xml` will almost certainly conflict because each branch has different versions (Kotlin, Spring, Tomcat, artifact version) while sharing the same structure. When resolving:

- Keep the **target branch's** version numbers (Spring, Tomcat, artifact version)
- Apply the **structural changes** from the source branch (new dependencies, plugin changes, etc.)
- Never blindly accept either side — read the diff carefully

Other files (`README.md`, `.gitignore`, Kotlin sources) may also conflict but are usually straightforward to resolve.

## Before pushing

Run `mvn test` on the current branch and ensure all tests pass before committing or pushing changes.

Create a git tag from the `<version>` in `pom.xml` with a `v` prefix. For example, version `1.0.0` → tag `v1.0.0`, version `1.0.0-spring-boot-2` → tag `v1.0.0-spring-boot-2`. Do this for each branch before pushing.
