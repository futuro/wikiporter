# Wikiporter

This project is meant to aid in transporting mediawiki data from one kind of
input source, such as a bzipped xml file, into some output target, such as a
database.

It is currently in the alpha stage of development. Contributions welcome!

## Installation

Download from https://github.com/futuro/wikiporter.

## Usage

    $ java -jar wikiporter-0.1.0-standalone.jar [args]

## Args

```
-c --config <config-path> Path to the config file
-i --input <input type> Currently only :bz2 is supported
-u --input-uri <uri> The URI to pass to the input component, e.g. wikisample.short.xml.bz2
-o --output <output type> Currently only :postgres is supported
-b --batch-size <batch size> The number of elements to parse at once, default 512
```

## Examples

See repl-example.clj for usage examples from the repl.

### Todo

* Create a cli frontend for wikiporter

* Move the xml->map functionality into the filters file. While the
  current functionality benefits me, as my current target app only
  needs a very shallow clone of every page, ultimately I need
  different kinds of filters, whatever they may be, to be swappable
  based on needs.

* To facilitate the easiest creation of new inputs and outputs, I
suspect it will be useful to have an agreed upon schema for inputs,
thus allowing output writers to create code that works regardless of
which input the user chooses. Since I have no idea how this will play
out practically, I'll have to wait until I need a new input, a new
output, or a differently structured output, such as a proper import of
the whole Wiktionary/Wikipedia data-set instead of only keeping single
revisions of each page.

## License

Copyright © 2015 Evan Niessen-Derry

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
