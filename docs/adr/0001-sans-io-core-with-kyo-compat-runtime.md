# Sans-IO core with a kyo-compat runtime

Sage targets multiple effect ecosystems (ZIO, cats-effect, Kyo, Ox) rather than a single one. To avoid writing the hardest code (sockets, reconnection, pipelining, pub/sub dispatch, cluster redirects) once per backend, the library is split into a pure sans-IO core (RESP protocol state machine, command model, codecs — no effect or I/O dependencies) and a single runtime layer written against [kyo-compat](https://github.com/getkyo/kyo/tree/main/kyo-compat), which cross-publishes per backend with `CIO`/`CStream` lowering to each ecosystem's native types at compile time.

## Considered Options

- **ZIO-native only** — most idiomatic for one ecosystem, but forfeits the multi-ecosystem positioning.
- **Sans-IO core + hand-written runtime per backend (proteus style)** — fully idiomatic per backend (typed errors, ZLayer, fs2 Resource), but the trickiest runtime code gets written and debugged 4×.
- **Sans-IO core + kyo-compat runtime (chosen)** — runtime written once. kyo-compat has no TCP/socket abstraction; sage provides its own internal socket layer (see ADR-0008), possibly upstreamed later. Errors are fixed to `Throwable` and there is no per-backend environment/context typing.

## Consequences

- The sans-IO core de-risks the kyo-compat bet: if kyo-compat proves insufficient, only the runtime layer is rewritten.
- ~~A TCP/socket abstraction must be contributed upstream to kyo-compat before the runtime can be built.~~ Superseded by ADR-0008: sage ships its own internal socket layer; upstreaming is optional and later.
