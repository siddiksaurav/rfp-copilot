import WorkScheduleClient from "../../components/WorkScheduleClient";

export default async function WorkSchedule({ searchParams }) {
    const { torName } = await searchParams || {};
    return <WorkScheduleClient torName={torName} />;
}