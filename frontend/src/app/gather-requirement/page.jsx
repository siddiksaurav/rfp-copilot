import GatherRequirementClient from '../../components/GatherRequirement';

export default async function GatherRequirementPage({ searchParams }) {
  // console.log(`GatherRequirementPage for torName: ${searchParams?.torName}`);
  const { torName } = await searchParams || {};
  return <GatherRequirementClient torName={torName} />;
}