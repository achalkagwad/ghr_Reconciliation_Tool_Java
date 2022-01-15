# Reconciliation_Tool_Java
 Slides describing a Reconciliation Tool which can be used for including but not limited to wide spread financial attribute reconciliations. Used to test and validate Data Integrity in Systems.
 
## Table of Contents
  * [What: Project Overview](#what-project-overview)
  * [Why: Motivation](#why-motivation)
  * [How: Process Involved](#how-process-involved)
  * [Project Installation Steps](#project-installation-steps)
  * [Directory Tree](#directory-tree)
  

## WHAT: Project Overview 
- This Reconciliation tool helps you to compare any flat file which could be used for wide spread financial attribute reconciliations. 
- Used to test and validate the “data Integrity” of Data in the same system or the flow of data between systems.
- Internally has support to set of configurations that are needed for comparisons and reconciliations of files.

<!-- ![image](https://user-images.githubusercontent.com/14209223/149622124-170e2c9e-e461-44f0-a3a2-2fb065284d3f.png) -->

## WHY: Motivation
This project was developed sensing the need of a easy to use reconciliation tool for banks in Singapore. I developed this project/tool end to end to offer this as a tool to banks and also thus get hired by banks and consultancies in Singapore.

## HOW: Process Involved
- I have made a **PPT PRESENTATION** of this project, can be viewed on **[SLIDESHARE_HERE](https://www.slideshare.net/achalkagwad/recon-tool)** . Present also under `/presentations` directory (See directory structure below)
- The presentation is quite self explanatory for somebody looking to reconcile and produce data from two different legacy systems.
- I used `Java` as the programming language, `APACHE POI` libraries to interact read and write data to excel.
- Use of proper object oriented design,exception handling, design patterns and SOLID principles of design in java code.

## Project Installation Steps
- Download the zip or fork the repo.
- If want to go through code and play around use any Java IDE like Eclipse or Intellij.
- Bat files and jar files in directory structure, use them to use this tool, while taking care of placing the input data file under `/data` structure.
- Place your config file under `/config` (See directory structure below and presentation for more details)
- Output data file- the required generated excel sheet/report comes under `/docs/gen`


<!--## Directory Tree 
```
├── app 
│   ├── __init__.py
│   ├── main.py
│   ├── model
│   ├── static
│   └── templates
├── config
│   ├── __init__.py
├── processing
│   ├── __init__.py
├── requirements.txt
├── runtime.txt
├── LICENSE
├── Procfile
├── README.md
└── wsgi.py
```
-->

## Directory Tree
```
|--   app
|     |-- Readme.md 
|     |-- src --  <-has all the source code well packaged into java packages and classes
|     |-- data <-- is where you put all the Input source and Target files
|     |-- config <- config is where you put your configFile and columnMapping and valueMapping files
|     |-- log <- gives a generated log file of the whole run of the project.
|     |-- docs <- Generated analysis as HTML, PDF, LaTeX, etc.
|           |-- gen <- Required Generated output excel sheet and figures to be used in reporting
|     |-- presentations <-Manually created presentations for business users,stake holders in pptx,pdf etc
|           |-- images <- Manual images obtained from various sources for presentation & other checkpoints
|     |-- Recon.bat
|     |-- Recon.jar
```
 
