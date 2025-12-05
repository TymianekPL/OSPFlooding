import {useEffect, useState} from "react";
import type {Property} from "csstype";
import "./App.css";

type APIColoursType = "OK" | "Fetching..." | "Unreachable";

const statusClasses: Record<APIColoursType, string> = {
     "OK": "bg-green-700 border-green-500 text-white",
     "Fetching...": "bg-yellow-200 border-yellow-400 text-black",
     "Unreachable": "bg-red-400 border-red-600 text-white"
};

function App() {
     const [status, setStatus] = useState<APIColoursType>("Fetching...");

     useEffect(() => {
          fetch("http://localhost:8080/health")
               .then(response => response.text())
               .then(statusText => setStatus(statusText as APIColoursType));
     });

     return (
          <div className="p-4">
               API Status:{" "}
               <span
                    className={`border-2 px-[15px] py-[4px] rounded-full ${statusClasses[status]}`}
               >
                    {status}
               </span>
          </div>
     );
}

export default App;
