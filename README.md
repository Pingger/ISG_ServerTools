# ISG_ServerTools

*Please read and respect the License.*  
This Project is intended for my own needs of functions on spigot servers (it initially started as a project for a bukkit server). There is quite a bit of **explorative Programming** involved, but I try to keep an eye on Security.

At the moment this Plugin is entirely controlled by the configuration File.

### General-Aim
 - Configuration changes are detected and automatically applied, without the requirement to reload the plugin or server (at the moment this is only partially the case)
 - Good configurability
 - Good Performance
 - A good command interface with Tab-Completion and Help-Pages
 - A general Purpose Plugin

### Feature-Aim
 - [x] Team
   - [ ] Allies and Enemies
   - [ ] Integration with Minecraft Teams
     - [x] Team to Minecraft-Team
     - [ ] Minecraft-Team to Team
   - [ ] Area Claiming
     - [ ] Area protection
     - [ ] Area effects
       - [ ] Glowing for Enemies
       - [ ] ...
     - [ ] Claiming with Banners
   - [x] Team-specific spawnlocation (needs some testing. Seems to sometimes not properly work on initial connect/death; Currently quite hacky)
 - [x] Chunk-Pregenerator
 - [x] Villager Protection (currently quite limited functionality and configurability)
 - [x] Sleep-Voting
 - [ ] Loot-Location generator (partially implemented)
   - [x] fixed location
   - [ ] Areas
   - [ ] Auto-Generating Structures
   - [ ] Custom-Loottables
   - [x] Minecraft-Loottables
   - [ ] ...
 - [ ] (Dyn-)map Structure Finder
 - [x] TPSMonitor (rework pending)
 
### Long-Term-Aims
 - Dynmap alternative
 - Some way for proper Unit-Testing. Ideas:
   - Implement Custom-Classes
   - run a normal server and issue a 'unit-test'-command, that outputs XML-Files with results (hacky ... don't really want to got this route)
 - Maria/MySQL-Database integration for better Datastorage
 - Cheat-Detection to allow Admins to better see who might be using hacks
   - possibly Anti-Cheat
   - Anti-Exploits (e.g. Anti-Item duping)
 
### Development
Pull-Requests are nice to have!
This repository is designed to be used in Eclipse! If you intend to develop on this project, please ensure you are following the Coding-Style, that is auto applied in this Eclipse-Project!  
A short summary of what you might want to do:
 - Use the @author-tag if you write an entire method/class or add it if you 'only' change some stuff in it.
   - if no author is specified for a method, the author of the usually class applies
 - Classes should use the @since-tag in the format "(upcoming plugin version) YYYY-MM-DD", to specify when this specific Version of the class was initially proposed to the project
 - The @version-tag for a version of the given class. the 1.0.0 version of the class denotes, that the class is finished for the first release.
A summary of the coding style:
 - Every Method, field, class, ..., that is NOT private is required to have proper JavaDoc (even if it might be VERY short)
 - Method-Braces on separate lines
 - Braces inside Methods:
   - Opening Brace same line
   - Closing Brace separate line, IF not a single-line-control statement (return/break/...), where it would put on the same-line
   - ALWAYS Brace, even on single-line-code (`if(true) { return; }`, not `if(true) return;`)
 - ... if you feel the need to have additional information here, please open an issue or pullrequest to get it added here