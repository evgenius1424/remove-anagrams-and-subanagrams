# Test Cases

The canonical test suite lives in `testcases/cases.json`. Each case contains:

- `id`: Unique integer identifier.
- `category`: Short label used for grouping and filtering.
- `input`: Input payload for the problem.
- `expected`: Expected output for the input.
- `explanation`: Human-readable description of what the case covers.

When adding cases:

- Keep `id` values stable so results are easy to compare across languages.
- Prefer small, focused examples that target one behavior at a time.
- Keep inputs and outputs JSON-friendly (arrays, strings, numbers, booleans).
