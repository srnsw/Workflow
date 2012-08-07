# Digital Archives Workflow Controller (DAWC)

A flexible platform for orchestrating digital preservation workflows.  Workflows are defined specific to particular migration projects. These workflows are submitted to the DAWC in a custom XML format along with digital records. The workflow tool then calls the different applications and web services as defined in that XML file. The workflow tool has both command line and web service interfaces. 

## Release Date

2012

## Copyright

Copyright (c) State of New South Wales through the State Records Authority of New South Wales, 2012

## License

Digital Archives Workflow Controller is released under the GNU General Public License (version 3 or later).

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program (see license).  If not, see <http://www.gnu.org/licenses/>.

## Requirements

FOR COMPLETE INFORMATION ABOUT INSTALLING AND RUNNING THIS APPLICATION, SEE THE USER GUIDE WHICH IS AVAILABLE ON THE DOWNLOAD PAGE (https://github.com/srnsw/Workflow/downloads).

Firstly:

- download the latest release package (from https://github.com/srnsw/Workflow/downloads)

Secondly, ensure that the following applications are available in the execution environmnent:

- Java
- ExifTool
- OpenOffice

Finally, set up the database:

- import the SQLdefinition file to a MySQL database by using MySQL Workbench.

## Run Digital Archives Workflow Controller (DAWC)

On Windows:

- double click daw.bat to start the application
- double click repoclient.bat to start the repository client.

On UNIX:

- configure the configuration files under the config path (see configuration for details)
- change permissions of the start up script by issuing the command chmod +x *.sh
- start the application by issuing the command ./daw.sh
- start the repository client by issuing the command ./repoclient.sh

Note: Make sure that the user starting the application has write permission on the running directory, the application produces log files at the root path of the running direcroty.
