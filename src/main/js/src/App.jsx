import {useEffect, useState} from 'react'
import Directories from './Directories.jsx'
import Files from './Files.jsx'

function App() {
    const [resDirs, setResDirs] = useState([])
    const [resFiles, setResFiles] = useState([])
    const getFilesAndDirectories = async (url) => {
        const response = await fetch(url);
        const resJson = await response.json();

        return resJson;
    }
    useEffect(() => {
        getFilesAndDirectories('./files').then(
            response => {
                setResDirs(response.directories)
                setResFiles(response.files)
            }
        )
    }, [])

    return (
        <>
            <div className='grid xl:grid-cols-4 md:grid-cols-3 sm:grid-cols-1'>
                <Directories
                    directories={resDirs}
                    setDirs={setResDirs}
                    setFiles={setResFiles}
                />
            </div>
            {
                (resDirs.length != 0 && resFiles.length != 0) &&
                <p>
                    <hr className="h-px my-8 mx-8 border-0 bg-slate-400 h-1"/>
                </p>

            }

            <div className='grid xl:grid-cols-4 md:grid-cols-3 sm:grid-cols-1'>
                <Files files={resFiles}/>
            </div>
        </>
    );
}

export default App
