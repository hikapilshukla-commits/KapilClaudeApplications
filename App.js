 const parent  = React.createElement("div",{id:"parent"}, 
 React.createElement("div",{id:"child"},React.createElement("h1",{},"I M  h1 tag")));

        const Root = ReactDOM.createRoot(document.getElementById("root"))
       Root.render(parent);
