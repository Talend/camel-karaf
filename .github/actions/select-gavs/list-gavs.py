#!/usr/bin/env python3
"""Print `groupId:artifactId:version` for the reactor modules in scope, without running Maven.

Usage: list-gavs.py <root-dir> <modules>   (<modules> = "ALL" or a comma-separated groupId:artifactId list)

Why not `mvn exec:exec`: it resolves each module's dependencies, so a module depending on another
NEW (not yet published, not yet built) GAV aborts the whole listing and every module after it
silently disappears from the check. Here the effective version is computed from the poms only
(own <version> or the parent's, ${revision} and other ${property} placeholders resolved).
Exits non-zero if a requested module is unknown or a version cannot be resolved.
"""
import os
import re
import sys
import xml.etree.ElementTree as ET

NS = "{http://maven.apache.org/POM/4.0.0}"


def child(el, name):
    return el.find(NS + name) if el is not None else None


def text(el, name):
    c = child(el, name)
    return c.text.strip() if c is not None and c.text else None


def load(pom_dir):
    root = ET.parse(os.path.join(pom_dir, "pom.xml")).getroot()
    parent = child(root, "parent")
    props = {}
    pe = child(root, "properties")
    if pe is not None:
        for p in pe:
            props[p.tag.replace(NS, "")] = (p.text or "").strip()
    return {
        "dir": pom_dir,
        "artifactId": text(root, "artifactId"),
        "groupId": text(root, "groupId") or text(parent, "groupId"),
        "version": text(root, "version") or text(parent, "version"),
        "parent": (text(parent, "groupId"), text(parent, "artifactId")) if parent is not None else None,
        "props": props,
        "modules": [m.text.strip() for m in root.findall(NS + "modules/" + NS + "module") if m.text],
    }


def walk(pom_dir, acc):
    mod = load(pom_dir)
    acc[(mod["groupId"], mod["artifactId"])] = mod
    for m in mod["modules"]:
        sub = os.path.join(pom_dir, m)
        if os.path.isfile(os.path.join(sub, "pom.xml")):
            walk(sub, acc)
    return acc


def props_of(mod, mods):
    """Properties visible from `mod`: its own over its ancestors' (reactor parents only)."""
    chain, seen = [], set()
    while mod and id(mod) not in seen:
        seen.add(id(mod))
        chain.append(mod["props"])
        mod = mods.get(mod["parent"]) if mod["parent"] else None
    merged = {}
    for p in reversed(chain):
        merged.update(p)
    return merged


def resolve(value, props, what):
    for _ in range(20):
        new = re.sub(r"\$\{([^}]+)\}", lambda m: props.get(m.group(1), m.group(0)), value)
        if new == value:
            break
        value = new
    if "${" in value:
        sys.exit("cannot resolve %s = %s" % (what, value))
    return value


def main():
    base, scope = sys.argv[1], sys.argv[2]
    mods = walk(base, {})
    if scope == "ALL":
        wanted = list(mods)
    else:
        wanted = []
        for ga in filter(None, (s.strip() for s in scope.split(","))):
            g, _, a = ga.partition(":")
            if (g, a) not in mods:
                sys.exit("module %s is not part of the reactor" % ga)
            wanted.append((g, a))
    for key in wanted:
        mod = mods[key]
        version = resolve(mod["version"] or "", props_of(mod, mods), "version of %s:%s" % key)
        print("%s:%s:%s" % (key[0], key[1], version))


if __name__ == "__main__":
    main()
