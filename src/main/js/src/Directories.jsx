export default function Directories(props) {
    const getFilesAndDirectories = async (url) => {
        const response = await fetch(url);
        const resJson = await response.json();

        return resJson;
    }
    const changeDirs = (newDir) => {
        getFilesAndDirectories(newDir)
            .then(
                res => {
                    props.setDirs(res.directories)
                    props.setFiles(res.files)
                }
            )
    };

    return (
        <>
            {props.directories.map(dir => {
                return (
                  <div
                    className="flex rounded bg-gray-900 text-gray-200 m-4 p-3 justify-center items-center flex-wrap"
                    key={dir.directoryName}
                  >
                    <p className="p-2 m-3 align-middle break-all">
                      [DIRECTORY] {dir.directoryName}
                    </p>
                      <br />
                    <button
                      onClick={() => {
                        changeDirs(dir.directoryPath);
                      }}
                      className="mx-2 bg-blue-700 text-blue-50 rounded-xl p-2 hover:bg-blue-100 hover:text-blue-800 w-full"
                    >
                      Open
                    </button>
                  </div>
                );
            })}
        </>
    );
}