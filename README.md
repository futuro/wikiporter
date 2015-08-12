# wikiporter

This project is meant to aid in transporting mediawiki data from one kind of
input source, such as a bzipped xml file, into some output target, such as a
database.

## Installation

Download from https://github.com/futuro/wikiporter.

## Usage

FIXME: explanation

    $ java -jar wikiporter-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Todo

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
