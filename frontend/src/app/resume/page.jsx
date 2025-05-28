import ResumeMakerClient from "../../components/ResumeMakerClient";

export default async function ResumeMaker({ searchParams }) {
    const { torName } = await searchParams || {};
    return <ResumeMakerClient torName={torName} />;
}