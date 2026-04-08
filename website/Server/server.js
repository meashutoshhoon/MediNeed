import "dotenv/config"
import cors from 'cors'
import express from 'express'
// import connectDB from "./config/db.js";

const app = express();
const PORT = process.env.PORT || 8000


// connectDB();
app.use(express.json())
app.use(express.urlencoded({ extended: true }));
const allowedOrigin = ['http://localhost:5173', ' http://localhost:5173/']
app.use(cors({origin: allowedOrigin, credentials: true}))



app.get('/', (req,res) => {
    res.send("Hello!")
})

app.listen(PORT, () => {
    console.log(`Sever is running on ${PORT}`);
    
})

 