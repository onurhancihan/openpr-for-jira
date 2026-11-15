#!/usr/bin/env python3
"""Zero-dependency checks for the Jira plugin descriptor and panel resources."""
from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
descriptor = ET.parse(root / "src/main/resources/atlassian-plugin.xml").getroot()
assert descriptor.tag == "atlassian-plugin"
assert descriptor.attrib.get("plugins-version") == "2"
panels = descriptor.findall("web-panel")
assert len(panels) == 1, "Exactly one Jira issue web-panel expected"
panel = panels[0]
assert panel.attrib["location"] == "atl.jira.view.issue.right.context"
view = next(r for r in panel.findall("resource") if r.attrib.get("name") == "view")
template = root / "src/main/resources" / view.attrib["location"].lstrip("/")
assert template.is_file(), f"Missing Velocity resource: {template}"
assert "GitHub Pull Requests" in template.read_text(encoding="utf-8")
assert "token" not in template.read_text(encoding="utf-8").lower()
pom = ET.parse(root / "pom.xml").getroot()
ns = {"m": "http://maven.apache.org/POM/4.0.0"}
assert pom.findtext("m:packaging", namespaces=ns) == "atlassian-plugin"
print("Static P2 descriptor and panel checks passed")
