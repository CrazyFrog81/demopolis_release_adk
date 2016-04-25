# README #

NOTE: this repository is currently being integrated into the http://github.com/arisona/ether repository. Until the new repo is stable, it's left here as an archived reference.

#Enabling DEMO:POLIS#

"Enabling DEMO:POLIS" is a participatory urban planning installation, presented as part of the DEMO:POLIS exhibition at the Berlin Akademie der Künste (http://www.adk.de/demopolis - 11.3.2016 - 29.5.2016). The installation engages the public in the design of open space and consists of six terminals that run a custom, interactive software application.

The software leads the user through a number of typical urban design tools (space allocation, streets, buildings, landscape, etc.) and concludes with a fly-through through the generated 3D scenario, in this case, the Rathausforum / Alexanderplatz area in Berlin.

The following video demonstrates a full cycle of a possible design.

https://youtu.be/sWgARvrcgxk

#Contact#

Stefan Arisona - http://robotized.arisona.ch


#Open Source#

Source code, data and a binary build are available at: http://github.com/arisona


#Credits#

Concept: Stefan Arisona, Ruth Conroy Dalton, Christoph Hölscher, Wilfried Wang

Data & Coding: Stefan Arisona, Simon Schubiger, Zeng Wei

Support: Akademie der Künste Berlin, FHNW Switzerland (Institute of 4D Technologies), ETH Zürich (Future Cities Laboratory and Chair of Cognitive Science), Northumbria University (Architecture and Built Environment).


#Data & Software Workflow#

Enabling DEMO:POLIS builds on Open Data, in particular the publicly available 3D models of central Berlin provided by the Staatssenat für Stadtentwicklung und Umwelt (http://www.stadtentwicklung.berlin.de/planen/stadtmodelle/)


The original 3D models were initially imported into Autodesk AutoCAD for layer selection and coordinate system adjustments, then imported into Autodesk Maya for data cleaning and corrections. In a final step the data was imported into Esri CityEngine for final data adjustments & cleaning, merging, labelling, etc. The data was then exported as OBJs. The software application is written in Java, based on the 3D graphics library/engine ether. As indicated above, all source code and data is available as open source.
