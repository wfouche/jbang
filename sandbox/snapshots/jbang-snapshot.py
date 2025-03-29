import sys
import os
import shutil
import hashlib
import datetime

indexFileText = """{
    "description": "__DESC__",
    "timestamp": "__TIMESTAMP__"
}
"""

def sha1_hash_file(filepath, output_filepath):
    """
    Calculates the SHA-1 hash of a file and stores it in another file.

    Args:
        filepath (str): The path to the input file.
        output_filepath (str): The path to the output file where the hash will be stored.
    """
    try:
        sha1_hash = hashlib.sha1()
        with open(filepath, 'rb') as f:
            while True:
                chunk = f.read(4096)
                if not chunk:
                    break
                sha1_hash.update(chunk)
        hash_value = sha1_hash.hexdigest()

        with open(output_filepath, 'w') as out_f:
            out_f.write(hash_value)

        print(f"    {filepath} --> {output_filepath}")

    except FileNotFoundError:
        print(f"Error: File not found at {filepath}")
    except Exception as e:
        print(f"An error occurred: {e}")

def getSources(scriptFilename):
    with open(scriptFilename, 'r') as file:
        for line in file:
            token = "//SOURCES "
            if len(line) > len(token):
                if line[0:len(token)] == token:
                    return line.split()[1:]
    return []

def main():
    # Get the main script filename
    mainScriptFilename = sys.argv[1]
    srcDir = os.path.dirname(mainScriptFilename)
    description = sys.argv[2]
    dateTimestamp = output_date = datetime.datetime.now().strftime("%Y-%m-%dT%H:%M:%S")

    # Display input parameters
    print("Source script   : " + mainScriptFilename)
    print("Description     : " + description)

    # Name of directory the mainScriptFilename resides in
    mainSnapshotDirname = os.path.splitext(mainScriptFilename)[0]


    # Create the snapshot folder if required
    if not os.path.isdir(mainSnapshotDirname):
        os.mkdir(mainSnapshotDirname)

    # Calculate the next snapshotId to  be used
    dir_list = os.listdir(mainSnapshotDirname)
    intSet = {0}
    for dirname in dir_list:
        vnum = int(dirname)
        intSet.add(vnum)
    snapshotId = max(intSet) + 1
    #print(snapshotId)

    # Create the snapshot folder
    destDir = mainSnapshotDirname + "/" + str(snapshotId)
    print("Snapshot folder : " + destDir)
    os.mkdir(destDir)

    # Copy files
    print("Snapshot started.")
    srcFiles = [mainScriptFilename]
    for file in getSources(mainScriptFilename):
        if len(srcDir) > 0:
            srcFiles.append(srcDir + '/' + file)
        else:
            srcFiles.append(file)

    for srcFile in srcFiles:
        print("    " + srcFile + " --> " + destDir + "/")
        shutil.copy(srcFile, destDir)
        destFile = destDir + '/' + os.path.basename(srcFile)
        #print(destFile)
        sha1DestFile = destDir + '/' + os.path.basename(srcFile) + '.sha1'
        sha1_hash_file(srcFile, sha1DestFile)
    srcFile = destDir + '/' + '00index.json'
    dstFile = srcFile + '.sha1'
    print("    " + srcFile)
    idxFile = open(srcFile, "w")
    idxFile.write(indexFileText.replace("__DESC__", description).replace("__TIMESTAMP__", dateTimestamp))
    idxFile.close()
    sha1_hash_file(srcFile, dstFile)
    print("Snapshot done.")

if __name__ == "__main__":
    main()
    sys.exit(0)
