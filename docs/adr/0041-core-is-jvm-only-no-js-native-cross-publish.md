# Core is JVM-only: no Scala.js / Scala Native cross-publish

The Core is the pure sans-IO protocol layer (ADR-0001), and the original PRD promised to cross-publish it for Scala.js and Scala Native "from day one" so an exotic-platform user could build their own runtime on top of it (PRD story 38). We are dropping that promise: the Core publishes for the JVM only, and no JS/Native artifact is produced. This supersedes the PRD's day-one cross-publish goal; the PRD has been edited to match.

## Why

The Core alone cannot talk to a server. Everything that does — sockets, the Multiplexed Connection, the dedicated pool, pub/sub dispatch, reconnect — lives in the Runtime, which is JVM-first by design (blocking I/O on virtual threads, ADR-0008). A JS/Native consumer of the Core would receive the RESP codec, `Command[Out]` values, and the cluster slot engine, but no way to connect them to a server; they would have to write an entire runtime themselves. No such consumer exists, and none is on the horizon. Cross-publishing now would ship a box of protocol parts nobody can use, while imposing real, perpetual cost: extra CI cells to maintain and a Core API constrained to the JS/Native-safe subset.

That last cost is concrete, not hypothetical. The Core's public API already uses `java.time.Instant` (expiry and stream-introspection replies) and `java.math.BigDecimal`, neither of which is available on Scala.js / Native without pulling in shim libraries (`scala-java-time` and a bignum shim) — which would break the Core's zero-dependency promise (ADR-0004) on those platforms or force those types out of the public API. Committing to JVM-only frees the Core to use them without apology.

## Consequences

- `java.time.Instant`, `java.math.BigDecimal`, `java.nio.*`, and other JVM-standard APIs are permanently fair game in the Core's public API; "platform-clean Core" is no longer a goal and there is no compile-only JS/Native guard.
- Reversing this — adding JS/Native later — is real work: it would require auditing and replacing those API uses (or accepting shim dependencies) on top of the build/CI setup. The trigger for reconsidering is a concrete consumer building a non-JVM runtime on the Core, not the abstract possibility.

## Considered Options

- **Publish JS/Native anyway as a strategic reach bet** — rejected: pays the API-constraint and CI cost now for a capability with zero consumers.
- **Keep the Core platform-clean without publishing** (rip `Instant`/`BigDecimal` out of the API, add a compile-only cross-build) — rejected: real API cost today to preserve a cheap future option nobody needs.
