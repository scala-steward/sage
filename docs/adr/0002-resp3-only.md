# RESP3 only

Sage speaks RESP3 exclusively, requiring Redis 6.0+ (2020) or any Valkey version. Supporting RESP2 as well would force dual reply-shape normalization across many commands and a legacy dedicated-connection pub/sub state machine; dropping it buys one typed reply model, push frames on the command connection, and client-side-caching support. Servers and proxies that only speak RESP2 (pre-6.0 Redis, twemproxy) are explicitly out of scope.
