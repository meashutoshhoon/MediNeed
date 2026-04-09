import Title from "../ui-components/title/Title";
import TeamBox from "./TeamBox";
import TeamInfo from "./TeamInfo";


import Aditya from "../../assets/about/aditya.jpeg";
import anirudha from "../../assets/about/anirudha.jpeg";
import ayush from "../../assets/about/ayush.jpeg";
import anvi from "../../assets/about/anvi.jpeg";
import Ashutosh from "../../assets/about/ashutosh.jpeg";


export default function About() {
  return (
    <section id="about" className="py-20 bg-black text-white">
      <div className="max-w-6xl mx-auto px-6 text-center">

        <Title title="About the Developer" />

        <p className="text-gray-400 text-sm mt-4 mb-12">
          Hi, We are developer passionate about building practical solutions. This medicine inventory app is designed to simplify <br/> stock management and help users track medicines efficiently with a clean and user-friendly interface.
        </p>

        {/* Grid instead of react-flexbox-grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">

          <TeamBox
            avatar={Ashutosh}
            name="Ashutosh Gupta"
            job="App Developer"
          />

          <TeamBox
            avatar={Aditya}
            name="Aditya Tomar"
            job="Full Stack Web Developer"
          />
          <TeamBox
            avatar={anirudha}
            name="Anirudh Khemriya"
            job="Automation Engineer"
          />
          <TeamBox
            avatar={ayush}
            name="Ayush Pathak"
            job="AI Developer"
          />
          <TeamBox
            avatar={anvi}
            name="Anvi Somani"
            job="UI UX Designer "
          />

          <TeamInfo />

        </div>

      </div>
    </section>
  );
}