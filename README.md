# Osu Songs Grabber
Automatically copies songs from your osu! songs folder to another folder by reading metadata in map files.
Great for Discord music bots or a local files library.

**Use at your own risk**, I have not done anything fancy apart from making it work.

# Instructions
- Copy the `SongCopier.java` code to some IDE
- Change the folder paths as marked by the comments
- Run the Program

# Format
Songs will become formatted as `SongName - ArtistName [DiffName].mp3` or `.ogg`

`[DiffName]` will only be appended if the mapset has multiple songs, because the metadata might not be the song's data (eg. Long Stream Practice Maps) while the diffName would be.

All illegal filename characters `\/:*?"<>|` will be replaced with the Deciduous Tree emoji `🌳`.
