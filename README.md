# Wikiporter

This project is meant to aid in transporting mediawiki data from one kind of
input source, such as a bzipped xml file, into some output target, such as a
database.

It is currently in the alpha stage of development. Contributions welcome!

## Current Limitations

While I've aimed to make Wikiporter easy to extend, my current use case only
requires shallow imports of pages. This results in importing seven key/value
pairs into a postgres database, those keys being title, ns, id, redirect,
revision_timestamp, revision\_text, and revision\_format. The revision\_* keys
come from the revision element inside of page elements, where I've flattened
the keys.

So, where as in the xml you would have something like
```
{:page
 ({:title String
  :revision
   ({:id BigInt}
    {:timestamp DateTime}
    etc etc
    )})}
```
you end up with a list of something like
```
({:title String
 :revision-id BigInt
 :revision-timestamp DateTime
 etc etc})
```

If you have a different use case than this, please feel free to open an issue
and/or a PR.

## Usage

    $ lein run -- [args]

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

* Investigate the use of schema's for easier coordination between inputs and
  outputs.

## License

Copyright © 2015 Evan Niessen-Derry

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
