export default function Files(props) {
    return (
        <>
            {props.files.map(file=>{
                return (
                  <div
                    className="flex rounded bg-stone-900 text-stone-200 m-4 p-3 justify-center items-center flex-wrap"
                    key={file.fileName}
                  >
                    <p className="m-3 p-2 align-middle break-all">[FILE] {file.fileName}</p>
                    <br />
                    <button
                      className="m-3  bg-red-700 text-red-50 rounded-3xl p-2 hover:bg-red-100 hover:text-red-800 w-full"
                      onClick={()=>{location.href = file.filePath}}
                    >
                      Download
                    </button>
                  </div>
                );
            })}
        </>
    );
}