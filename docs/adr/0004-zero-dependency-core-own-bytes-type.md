# Zero-dependency core with its own Bytes type

`sage-core` has zero external dependencies — a deliberate selling point for a foundational library that will sit in ZIO, cats-effect, Kyo, and Ox dependency trees. Consequently the byte container in codec and protocol APIs is sage's own `opaque type Bytes = IArray[Byte]` rather than scodec-bits' `ByteVector`, which was the ergonomically superior alternative (structural equality, cheap slices). Known cost: universal `==` on `Bytes` is reference equality and unreliable — the API provides explicit `sameBytes` and documents the footgun loudly.
