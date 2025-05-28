import GatherRequirementClient from '../../components/GatherRequirement';

export default async function GatherRequirementPage({ searchParams }) {
  const { torName } = await searchParams || {};
  return <GatherRequirementClient torName={torName} />;
}